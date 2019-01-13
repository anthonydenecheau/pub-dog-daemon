package com.scc.pub.repository;

import java.util.List;

import com.scc.pub.model.SyncData;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


/**
 * Created by AnthonyLenovo on 06/01/2019.
 */

@Repository
public interface OdsDataRepository extends CrudRepository<SyncData, String> {
    public List<SyncData> findByTransfertAndDomaine(String transfert, String domaine);

    public SyncData save(SyncData dog);

    public void delete(SyncData dog);
}