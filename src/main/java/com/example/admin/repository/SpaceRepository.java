package com.example.admin.repository;

import com.example.admin.entity.Space;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpaceRepository extends JpaRepository<Space,Long> {
}
