package com.scc.pub.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.scc.pub.model.Owner;

@Repository
public interface OwnerRepository extends CrudRepository<Owner, String> {
	public List<Owner> findById(int id);

	public Owner findByIdDog(int idDog);
}