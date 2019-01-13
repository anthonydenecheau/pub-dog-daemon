package com.scc.pub.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.scc.pub.model.Pedigree;

@Repository
public interface PedigreeRepository extends CrudRepository<Pedigree, String> {
    public Pedigree findById(long id);
}