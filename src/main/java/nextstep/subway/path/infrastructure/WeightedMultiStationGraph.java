package nextstep.subway.path.infrastructure;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.exception.NotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.path.domain.Path;
import nextstep.subway.path.domain.StationGraph;
import nextstep.subway.station.domain.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Component;

@Component
public class WeightedMultiStationGraph implements StationGraph {

    private WeightedMultigraph<Station, SectionEdge> graph;

    public WeightedMultiStationGraph() {
        this.graph = new WeightedMultigraph<>(SectionEdge.class);
    }

    @Override
    public StationGraph createGraph(List<Line> lines) {
        for (Line line : lines) {
            addVertex(line.getStations());
            addEdgeWeight(line.getSections(), line);
        }
        return this;
    }

    @Override
    public boolean containsStation(Station station) {
        return graph.containsVertex(station);
    }

    @Override
    public Path getShortestPath(Station source, Station target) {
        DijkstraShortestPath<Station, SectionEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
        GraphPath<Station, SectionEdge> path = dijkstraShortestPath.getPath(source, target);
        if (path == null) {
            throw new NotFoundException("접점이 없습니다.");
        }
        return Path.fromIncludedLines(
            path.getVertexList(),
            Double.valueOf(path.getWeight()).intValue(),
            getIncludedLines(path));
    }

    private List<Line> getIncludedLines(GraphPath<Station, SectionEdge> path) {
        List<SectionEdge> edgeList = path.getEdgeList();
        return edgeList.stream()
            .map(it -> it.getLine())
            .distinct()
            .collect(Collectors.toList());
    }

    private void addVertex(List<Station> stations) {
        for (Station s : stations) {
            graph.addVertex(s);
        }
    }

    private void addEdgeWeight(List<Section> sections, Line line) {
        for (Section s : sections) {
            SectionEdge sectionEdge = new SectionEdge(s, line);
            graph.addEdge(sectionEdge.getSource(), sectionEdge.getTarget(), sectionEdge);
            graph.setEdgeWeight(sectionEdge, sectionEdge.getWeight());
        }
    }
}