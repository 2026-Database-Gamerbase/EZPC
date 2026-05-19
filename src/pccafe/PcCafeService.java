package pccafe;

import java.sql.SQLException;
import java.util.List;

public class PcCafeService {
    private final PcCafeDAO pcCafeDAO;

    public PcCafeService() {
        this(new PcCafeDAOImpl());
    }

    public PcCafeService(PcCafeDAO pcCafeDAO) {
        this.pcCafeDAO = pcCafeDAO;
    }

    public void insertPcCafe(PcCafe pcCafe) throws SQLException {
        validateStarRating(pcCafe.getAverageStarRating());
        pcCafeDAO.insert(pcCafe);
    }

    public PcCafe getPcCafe(String pcId) throws SQLException {
        return pcCafeDAO.findById(pcId);
    }

    public List<PcCafe> getAllPcCafes() throws SQLException {
        return pcCafeDAO.findAll();
    }

    public void updatePcCafe(PcCafe pcCafe) throws SQLException {
        validateStarRating(pcCafe.getAverageStarRating());
        pcCafeDAO.update(pcCafe);
    }

    public void deletePcCafe(String pcId) throws SQLException {
        pcCafeDAO.deleteById(pcId);
    }

    private void validateStarRating(double averageStarRating) {
        if (averageStarRating < 0.0 || averageStarRating > 5.0) {
            throw new IllegalArgumentException("Star rating must be between 0.0 and 5.0.");
        }
    }
}
