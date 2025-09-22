package com.laundreader.domain.repository.laundry;

import org.springframework.data.jpa.repository.JpaRepository;

import com.laundreader.domain.entity.laundry.Laundry;

public interface LaundryRepository extends JpaRepository<Laundry, Long> {
}
