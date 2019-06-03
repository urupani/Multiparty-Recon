package com.demo.MultipartyRecon.Repository;


import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;


import com.demo.MultipartyRecon.Entity.BankTxnEntity;


public interface BankTxnRepository extends CrudRepository<BankTxnEntity, Long>{
	
	@Query(nativeQuery=true, value="select * from bank_transaction_entity")
	public Iterable<BankTxnEntity> findAll();
}

