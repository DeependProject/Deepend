package com.minecade.deepend.storage.transactions;

/**
 * Created 2/27/2016 for Deepend
 *
 * @author Citymonstret
 */
@FunctionalInterface
public interface PendingTransaction {
    Object performTransaction();
}