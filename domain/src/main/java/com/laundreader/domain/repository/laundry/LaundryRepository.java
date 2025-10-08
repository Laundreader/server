package com.laundreader.domain.repository.laundry;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.laundreader.domain.entity.laundry.Laundry;

public interface LaundryRepository extends JpaRepository<Laundry, Long> {
	Optional<Object> findByIdAndUserId(Long laundryId, Long userId);

	List<Laundry> findAllByUserId(Long userId);

	List<Laundry> findAllByIdInAndUserId(List<Long> laundryIds, Long userId);

	List<Laundry> findAllByUserIdOrderByCreatedAtDesc(Long userId);

}
