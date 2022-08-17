package com.promineotech.jeep.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import com.promineotech.jeep.controller.support.FetchJeepTestSupport;
import com.promineotech.jeep.entity.Jeep;
import com.promineotech.jeep.entity.JeepModel;

/*
 * tell test to run in the webEnvironment
 * when testing, the host name is always local host so the request
 * doesn't go out of the local network
 * 
 * specify random port in base test class so it's available for all test
 * Will create a new profile and override the default and SB will look for application-test.yaml in testresources
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Sql(scripts = {"classpath:flyway/migrations/V1.0__Jeep_Schema.sql", 
     "classpath:flyway/migrations/V1.1__Jeep_Data.sql"},
    config = @SqlConfig(encoding = "utf-8"))
class FetchJeepTest extends FetchJeepTestSupport {

  
  @Test
 // test should be self describing
  void testThatJeepsAreReturnedWhenAValidModelAndTrimAreSupplied() {
 // Given: A valid model, trim, and URI
    JeepModel model = JeepModel.WRANGLER;
    String trim = "Sport";
    String uri = String.format("%s?model=%s&trim=%s", getBaseUri(), model, trim);
    //note: %s is the placeholder for the getBaseUri method from the BaseTest class
    
     
 // When: a connection is made to the URI
    /*
     * throw http at rest controller, sends http request to service layer, 
     * receives the response and formats it to go back to the client
     */
    ResponseEntity<List<Jeep>> response = getRestTemplate().exchange(uri, HttpMethod.GET, null, 
        new ParameterizedTypeReference<>() {});
   
   
 // Then: a valid status code (OK - 200) is returned 
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    
    
 // And : the actual list returned is the same as the expected list
    List<Jeep> actual = response.getBody();
    List<Jeep> expected = buildExpected();
    
    /*
     * loop through the returned actual value and set the PrimaryKey to null
     */
    // not good approach - actual.forEach(jeep -> jeep.setModelPK(null));
    
    assertThat(actual).isEqualTo(expected);
    
  }
  
}
