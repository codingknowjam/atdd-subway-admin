package nextstep.subway.line.domain;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.exception.InvalidSectionException;
import nextstep.subway.station.domain.Station;

@Entity
public class Section extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Line line;

    @ManyToOne(fetch = FetchType.LAZY)
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    private Station downStation;

    private int distance;

    protected Section() {
    }

    public Section(Station upStation, Station downStation, int distance) {
        validateSection(upStation, downStation, distance);

        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    private void validateSection(Station upStation, Station downStation, int distance) {
        if (Objects.isNull(upStation) || Objects.isNull(downStation)) {
            throw new IllegalArgumentException("양 끝 Station이 반드시 존재해야 합니다.");
        }

        if (distance <= 0) {
            throw new IllegalArgumentException("거리값은 반드시 0보다 커야 합니다.");
        }
    }

    public Station upStation() {
        return upStation;
    }

    public Station downStation() {
        return downStation;
    }

    public boolean isLongerThan(Section section) {
        return this.distance > section.distance;
    }

    public boolean mergeable(Section section) {
        return upStation == section.upStation
                ^ downStation == section.downStation;
    }

    public Section reducedBy(Section section) {
        validateReducible(section);

        if (this.upStation == section.upStation) {
            return new Section(
                section.downStation, this.downStation, this.distance - section.distance);
        }

        return new Section(
            this.upStation, section.upStation, this.distance - section.distance);
    }

    private void validateReducible(Section section) {
        if (!this.mergeable(section)) {
            throw new InvalidSectionException("구간을 축소하려면 병합 가능해야 합니다");
        }

        if (!this.isLongerThan(section)) {
            throw new InvalidSectionException("0 이하로 축소할 수 없습니다.");
        }
    }

    public void setLine(Line line) {
        validateLine(line);
        this.line = line;
    }

    private void validateLine(Line line) {
        if (Objects.isNull(line)) {
            throw new IllegalArgumentException("소속 노선은 null 이 될 수 없습니다.");
        }

        if (!Objects.isNull(this.line)) {
            throw new InvalidSectionException("소속 노선을 재 정의 할 수는 없습니다.");
        }

        if (!line.contains(this)) {
            throw new InvalidSectionException("해당 노선과의 연관관계가 확인되지 않습니다.");
        }
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
