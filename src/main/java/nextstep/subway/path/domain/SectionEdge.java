package nextstep.subway.path.domain;

import nextstep.subway.line.domain.Section;
import org.jgrapht.graph.DefaultWeightedEdge;

public class SectionEdge extends DefaultWeightedEdge {

    private Section section;

    public SectionEdge(Section section) {
        this.section = section;
    }

    @Override
    protected Object getSource() {
        return section.getUpStation();
    }

    @Override
    protected Object getTarget() {
        return section.getDownStation();
    }

    @Override
    protected double getWeight() {
        return section.getDistanceAsDouble();
    }

    public int getLineSurcharge() {
        return section.getLine().getSurcharge();
    }
}