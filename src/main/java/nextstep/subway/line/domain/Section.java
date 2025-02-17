package nextstep.subway.line.domain;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Station;

@Entity
public class Section extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "lineId")
	private Line line;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "upStationId")
	private Station upStation;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "downStationId")
	private Station downStation;

	private int distance;

	private int sequence;

	protected Section() {
	}

	private Section(Long id, Station upStation, Station downStation, int distance) {
		this.id = id;
		this.upStation = upStation;
		this.downStation = downStation;
		this.distance = distance;
	}

	public static Section create(Station upStation, Station downStation, int distance) {
		return new Section(null, upStation, downStation, distance);
	}

	public static Section create(Long id, Station upStation, Station downStation, int distance) {
		return new Section(id, upStation, downStation, distance);
	}

	public Station getUpStation() {
		return upStation;
	}

	public Station getDownStation() {
		return downStation;
	}

	public int getSequence() {
		return sequence;
	}

	public int getDistance() {
		return distance;
	}

	public Long getId() {
		return id;
	}

	public void initSequence(Line line) {
		this.sequence = line.sectionsSize();
	}

	public void initLine(Line line) {
		this.line = line;
	}

	public void updateSequence(int sequence) {
		this.sequence = sequence;
	}

	public void updateUpStation(Station station) {
		this.upStation = station;
	}

	public void updateDistance(int distance) {
		this.distance = distance;
	}

	public void updateDownStation(Station station) {
		this.downStation = station;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Section section = (Section)o;
		return Objects.equals(id, section.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

}
