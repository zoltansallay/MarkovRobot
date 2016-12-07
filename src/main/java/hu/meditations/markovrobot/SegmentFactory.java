package hu.meditations.markovrobot;

public class SegmentFactory {

    public static Segment createSegment(String[] data) throws Exception {
        String segmentType = data[0];
        if (segmentType.contentEquals("SegmentLine")) {
            return (Segment) new SegmentLine(data);
        } else {
            throw(new Exception("Unknown segment type: " + data[0]));
        }
    }

}
