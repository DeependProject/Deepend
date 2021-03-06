/*
 * Copyright 2016 Minecade
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.minecade.deepend.server.storage;

import com.minecade.deepend.data.DataHolder;
import com.minecade.deepend.data.DataObject;
import com.minecade.deepend.logging.Logger;
import com.minecade.deepend.resources.DeependBundle;
import com.minecade.deepend.storage.StorageBase;
import com.minecade.deepend.storage.transactions.PendingTransaction;
import com.minecade.deepend.storage.transactions.TransactionPair;
import com.minecade.deepend.storage.transactions.TransactionResult;
import lombok.SneakyThrows;
import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.SqlJetTransactionMode;
import org.tmatesoft.sqljet.core.schema.SqlJetConflictAction;
import org.tmatesoft.sqljet.core.table.ISqlJetTable;
import org.tmatesoft.sqljet.core.table.SqlJetDb;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Created 2/27/2016 for Deepend
 * <p>
 * ********************************************
 * This is real unstable, shouldn't be used  *
 * ********************************************
 *
 * @author Citymonstret
 */
public class SQLite implements StorageBase
{

    public static final String TABLE_NAME = "persistent_storage";

    private final File databaseFile;
    private final Collection<TransactionPair> transactions = Collections.synchronizedSet( new LinkedHashSet<>() );
    private SqlJetDb database;
    private Thread sqliteThread;
    private volatile boolean shutdownRequested = false;

    @SneakyThrows
    public SQLite(String fileName)
    {
        this.databaseFile = new File( DeependBundle.folder, fileName + ".db" );
        if ( !this.databaseFile.exists() )
        {
            if ( !( this.databaseFile.createNewFile() ) )
            {
                Logger.get().error( "Failed to create SQLite Database: " + fileName + ", please create it manually" );
            }
        }
    }

    @SneakyThrows(IllegalAccessException.class)
    public void addPendingTransaction(PendingTransaction transaction, TransactionResult result)
    {
        if ( shutdownRequested )
        {
            throw new IllegalAccessException( "Cannot add transactions when the database is shutting down" );
        }
        this.transactions.add( new TransactionPair( transaction, result ) );
    }

    @Override
    public void setup()
    {
        try
        {
            this.database = SqlJetDb.open( databaseFile, true );
            Logger.get().info( "SQLite Connection Opened" );
        } catch ( SqlJetException e )
        {
            Logger.get().error( "Failed to open SQLite Connection", e );
        }

        this.sqliteThread = new Thread( "SQLiteRunner" )
        {
            @Override
            public void run()
            {
                try
                {
                    database.beginTransaction( SqlJetTransactionMode.WRITE );
                    try
                    {
                        database.createTable( "CREATE TABLE IF NOT EXISTS `persistent_storage` (`entry_id` INTEGER PRIMARY KEY, `path` TEXT NOT NULL, `object_key` TEXT NOT NULL, `object_value` TEXT NOT NULL), unique(`path`, `object_key`)" );
                    } catch ( SqlJetException ee )
                    {
                        Logger.get().error( "Failed to execute main table creation statement", ee );
                    } finally
                    {
                        database.commit();
                    }
                } catch ( final Exception e )
                {
                    Logger.get().error( "Something went wrong when creating the main table", e );
                }

                List<TransactionPair> toRemove = new ArrayList<>();
                while ( !shutdownRequested || database.isInTransaction() || !transactions.isEmpty() )
                {
                    if ( database.isInTransaction() )
                    {
                        continue; // Only start transactions when supposed to
                    }
                    toRemove.clear();
                    for ( TransactionPair transaction : SQLite.this.transactions )
                    {
                        try
                        {
                            transaction
                                    .getTransactionResult()
                                    .performAction( transaction
                                            .getPendingTransaction()
                                            .performTransaction() );
                        } catch ( final Exception e )
                        {
                            Logger.get().error( "Failed to perform transaction", e );
                        }
                        toRemove.add( transaction );
                    }
                    transactions.removeAll( toRemove );
                }
            }
        };

        sqliteThread.setDaemon( false );
        sqliteThread.start();
    }

    @Override
    public void close()
    {
        shutdownRequested = true;
        // Just wait 10ms before we continue
        // this is to make sure that we don't close in the middle
        // of a transaction
        while ( sqliteThread.isAlive() )
        {
            try
            {
                Thread.sleep( 10 );
            } catch ( InterruptedException e )
            {
                e.printStackTrace();
            }
        }
        if ( this.database != null )
        {
            if ( this.database.isOpen() )
            {
                try
                {
                    this.database.close();
                } catch ( SqlJetException e )
                {
                    Logger.get().error( "Database closing attempt failed; Error", e );
                }
            } else
            {
                Logger.get().error( "Database closing attempt failed; Database wasn't open" );
            }
        } else
        {
            Logger.get().error( "Database closing attempt failed; Database was null" );
        }
    }

    @Override
    public void getDataObject(String path)
    {

    }

    @Override
    public void getDataHolder(String path)
    {

    }

    @Override
    public void saveDataHolder(String path, DataHolder holder)
    {
        addPendingTransaction( () -> {
            try
            {
                database.beginTransaction( SqlJetTransactionMode.WRITE );
                int inserted = 0;
                try
                {
                    final ISqlJetTable table = database.getTable( TABLE_NAME );
                    for ( Object o : holder.values() )
                    {
                        if ( !( o instanceof DataObject ) )
                        {
                            continue;
                        }
                        DataObject object = (DataObject) o;
                        table.insertOr( SqlJetConflictAction.REPLACE, path + "." + holder.getIdentifier(), object.getName(), object.getValue() );
                        inserted++;
                    }
                } catch ( final Exception e )
                {
                    Logger.get().error( "Failed to insert holder values", e );
                } finally
                {
                    database.commit();
                }
                return inserted;
            } catch ( final Exception e )
            {
                e.printStackTrace();
            }
            return false;
        }, (o) -> Logger.get().debug( "Successfully inserted: " + o + " values" ) );
    }

    private ISqlJetTable getTable() throws Exception
    {
        return database.getTable( TABLE_NAME );
    }

    @Override
    public void saveDataObject(String path, DataObject object)
    {
        addPendingTransaction( () -> {
            try
            {
                boolean success = true;
                database.beginTransaction( SqlJetTransactionMode.WRITE );
                try
                {
                    final ISqlJetTable table = getTable();
                    table.insertOr( SqlJetConflictAction.REPLACE, path, object.getName(), object.getValue() );
                } catch ( final Exception e )
                {
                    Logger.get().error( "Failed to insert object value", e );
                    success = false;
                } finally
                {
                    database.commit();
                }
                return success;
            } catch ( final Exception e )
            {
                e.printStackTrace();
            }
            return false;
        }, (o) -> {
            if ( (boolean) o )
            {
                Logger.get().debug( "Yay! Insert of data object went well" );
            } else
            {
                Logger.get().debug( "Well, awkward..." );
            }
        } );
    }
}
