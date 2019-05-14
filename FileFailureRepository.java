package com.demo.MultipartyRecon.Repository;

import org.springframework.data.repository.CrudRepository;

import com.demo.MultipartyRecon.Entity.FileHistoryEntity;

public interface FileFailureRepository extends CrudRepository<FileHistoryEntity, Long> {

}
