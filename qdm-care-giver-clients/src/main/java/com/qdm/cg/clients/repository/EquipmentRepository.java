package com.qdm.cg.clients.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.qdm.cg.clients.entity.Equipment;
import com.qdm.cg.clients.entity.Issues;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {

	public List<Equipment> findByClientId(long clientId);

}
