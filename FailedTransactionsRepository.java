package com.demo.MultipartyRecon.Repository;

import org.springframework.data.repository.CrudRepository;

import com.demo.MultipartyRecon.Entity.FailedTransactionsEntity;

public interface FailedTransactionsRepository extends CrudRepository<FailedTransactionsEntity, Long> {

}
