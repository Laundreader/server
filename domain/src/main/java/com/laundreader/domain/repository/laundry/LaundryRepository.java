package com.laundreader.domain.repository.laundry;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.laundreader.domain.entity.laundry.Laundry;

public interface LaundryRepository extends JpaRepository<Laundry, Long> {
	Optional<Object> findByIdAndUserId(Long laundryId, Long userId);
}
