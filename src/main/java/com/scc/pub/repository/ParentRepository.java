package com.scc.pub.repository;

import com.scc.pub.model.Parent;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParentRepository extends CrudRepository<Parent, String> {
	public Parent findById(int id);
}
