/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.flowable.ui.application;

import org.flowable.idm.api.IdmIdentityService;
import org.flowable.idm.api.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Filip Hrisafov
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(FlowableUiIdmApplicationDefaultAuthenticationTest.TestBootstrapConfiguration.class)
public class FlowableUiIdmApplicationDefaultAuthenticationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void authenticate() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("j_username", "my-admin");
        map.add("j_password", "my-pass");
        ResponseEntity<String> authenticationResponse = restTemplate
            .postForEntity("/app/authentication", new HttpEntity<>(map, headers), String.class);

        assertThat(authenticationResponse.getStatusCode()).as(authenticationResponse.toString()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void authenticateWithWrongPassword() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("j_username", "my-admin");
        map.add("j_password", "wrong");
        ResponseEntity<String> authenticationResponse = restTemplate
            .postForEntity("/app/authentication", new HttpEntity<>(map, headers), String.class);

        assertThat(authenticationResponse.getStatusCode()).as(authenticationResponse.toString()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void authenticateWithNonExistingUser() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("j_username", "non-existing");
        map.add("j_password", "wrong");
        ResponseEntity<String> authenticationResponse = restTemplate
            .postForEntity("/app/authentication", new HttpEntity<>(map, headers), String.class);

        assertThat(authenticationResponse.getStatusCode()).as(authenticationResponse.toString()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @TestConfiguration
    static class TestBootstrapConfiguration {

        @Bean
        public CommandLineRunner initTestUsers(IdmIdentityService idmIdentityService) {
            return args -> {
                User testUser = idmIdentityService.createUserQuery().userId("my-admin").singleResult();
                if (testUser == null) {
                    createTestUser(idmIdentityService);
                }
            };
        }

        private void createTestUser(IdmIdentityService idmIdentityService) {
            User user = idmIdentityService.newUser("my-admin");
            user.setPassword("my-pass");
            idmIdentityService.saveUser(user);
        }
    }
}
