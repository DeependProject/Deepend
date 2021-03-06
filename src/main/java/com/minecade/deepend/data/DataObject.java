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

package com.minecade.deepend.data;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * A simple data object
 *
 * @author Citymonstret
 */
public class DataObject
{

    @NonNull
    @Getter
    private String name;

    @NonNull
    private String value;

    @Getter(AccessLevel.PRIVATE)
    @Setter
    private DataHolder holder;

    @Getter
    @Setter
    private boolean deleted = false;

    /**
     * Constructor
     *
     * @param name  Data Key
     * @param value Data Value
     */
    public DataObject(final String name, final String value)
    {
        this.name = name;
        this.value = value;
    }

    @Override
    final public String toString()
    {
        return this.getName();
    }

    /**
     * Get the value
     *
     * @return Data Value
     */
    final public String getValue()
    {
        return this.processValue( value );
    }

    /**
     * Can be used to alter the values
     * Will return the input value,
     * unless the method has been
     * overridden
     *
     * @param value Value to process
     * @return Value
     */
    public String processValue(String value)
    {
        return value;
    }

    /**
     * Delete the object
     */
    public void delete()
    {
        getHolder().remove( name );
    }

    /**
     * Write the value of this
     * object to a buf
     *
     * @param buf Buf to write to
     */
    public void write(DeependBuf buf)
    {
        buf.writeString( this.value );
    }
}
