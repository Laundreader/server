package com.laundreader.domain.entity.laundry;

import java.util.List;

import com.laundreader.domain.converter.LaundrySymbolConverter;
import com.laundreader.domain.converter.SolutionConverter;
import com.laundreader.domain.converter.StringListConverter;
import com.laundreader.domain.dto.laundry.LaundrySymbolDTO;
import com.laundreader.domain.dto.laundry.SolutionDTO;
import com.laundreader.domain.entity.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "laundry_tb")
public class Laundry {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 소재 배열
	@Column(columnDefinition = "TEXT")
	@Convert(converter = StringListConverter.class)
	private List<String> materials;  // List<String> <-> JSON

	private String color;

	@Column(name = "clothes_type")
	private String type;

	@Column(nullable = false)
	private boolean hasPrintOrTrims;

	// 추가 정보 배열
	@Column(columnDefinition = "TEXT")
	@Convert(converter = StringListConverter.class)
	private List<String> additionalInfo;  // List<String> <-> JSON

	// 세탁 기호 배열
	@Column(columnDefinition = "TEXT")
	@Convert(converter = LaundrySymbolConverter.class)
	private List<LaundrySymbolDTO> laundrySymbols;        // List<LaundrySymbolDTO> <-> JSON

	// 솔루션 배열
	@Column(columnDefinition = "TEXT")
	@Convert(converter = SolutionConverter.class)
	private List<SolutionDTO> solutions;    // List<SolutionDTO> <-> JSON

	private String labelUrl;
	private String clothesUrl;
	private String thumbnailUrl;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
}
