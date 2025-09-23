package com.pht.utils;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Transaction;
import org.hibernate.engine.transaction.internal.TransactionImpl;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
public class HibernateUtils {

    public static Transaction getCurrentTransaction() {
        Transaction transaction = null;
        try {
            transaction = TransactionSynchronizationManager.getResourceMap().keySet().stream()
                    .filter(key -> key instanceof EntityManagerFactory)
                    .findFirst()
                    .map(key -> {
                        Transaction tx = null;
                        Object object = TransactionSynchronizationManager.getResourceMap().get(key);
                        if (object instanceof EntityManagerHolder entityManagerHolder) {
                            EntityTransaction entityTransaction = entityManagerHolder.getEntityManager().getTransaction();
                            if (entityTransaction != null) {
                                tx = (TransactionImpl) entityTransaction;
                            }
                        }
                        return tx;
                    })
                    .orElse(null);
        } catch (Exception ex) {
            log.trace(ex.getMessage(), ex);
        }

        return transaction;
    }

    public static int getTransactionId(Transaction tx) {
        return System.identityHashCode(tx);
    }

    public static int getCurrentTransactionId() {
        return getTransactionId(getCurrentTransaction());
    }
}

