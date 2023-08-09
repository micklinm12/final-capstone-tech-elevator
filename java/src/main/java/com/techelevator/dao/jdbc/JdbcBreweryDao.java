package com.techelevator.dao.jdbc;

import com.techelevator.dao.BreweryDao;
import com.techelevator.dao.jdbc.mapper.BreweryMapper;
import com.techelevator.exception.ResourceNotFoundException;
import com.techelevator.model.Brewery;
import com.techelevator.exception.DaoException;
import com.techelevator.openbrewerydb.model.OpenBreweryDTO;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcBreweryDao implements BreweryDao {

    private final JdbcTemplate jdbcTemplate;

    private final BreweryMapper breweryMapper = new BreweryMapper();

    public JdbcBreweryDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<Brewery> getBreweries() {
        String sql = "SELECT * FROM brewery";
        try {
            return jdbcTemplate.query(sql, breweryMapper);
        } catch (Exception ex) {
            throw new DaoException(ex.getMessage());
        }

    }

    public Brewery getBreweryById(Integer id) throws ResourceNotFoundException {
        String sql = "SELECT * FROM brewery WHERE brewery_id = ?";

        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{id}, breweryMapper);
        } catch (EmptyResultDataAccessException ex) {
            throw new ResourceNotFoundException("Couldn't find brewery with id " + id);
        } catch (Exception ex) {
            throw new DaoException(ex.getMessage());
        }
    }

    public Brewery getBreweryByOpenDbId(String openDbId) {
        String sql = "SELECT * FROM brewery WHERE open_db_id = ?";

        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{openDbId}, breweryMapper);
        } catch (EmptyResultDataAccessException ex) {
            return null;
        } catch (Exception ex) {
            throw new DaoException(ex.getMessage());
        }
    }


    public void addBreweryFromOpenDb(OpenBreweryDTO brewery) {
        String sql = "INSERT INTO brewery (brewery_id, open_db_id, brewery_name, brewery_type, phone_number, website, street_address_1, " +
                "street_address_2, city, state_province, postal_code, latitude, longitude, country) " +
                "VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Double latitude = null;
        Double longitude = null;
        try {
            if (brewery.getLatitude() != null && brewery.getLongitude() != null) {
                latitude = Double.parseDouble(brewery.getLatitude());
                longitude = Double.parseDouble(brewery.getLongitude());
            }

            jdbcTemplate.update(sql, brewery.getId(),
                    brewery.getName(), brewery.getBreweryType(), brewery.getPhone(), brewery.getWebsiteUrl(),
                    brewery.getAddress1(), brewery.getAddress2(), brewery.getCity(), brewery.getStateProvince(),
                    brewery.getPostalCode(), latitude, longitude, brewery.getCountry());
            return;
        } catch (Exception ex) {
            throw new DaoException(ex.getMessage());
        }
    }




}
