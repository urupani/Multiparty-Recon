package com.demo.MultipartyRecon.Repository;
import org.springframework.data.repository.CrudRepository;

import com.demo.MultipartyRecon.Entity.BankTransactionEntity;

public interface MultipartyReconRepository extends CrudRepository<BankTransactionEntity,Long>{
	
}
