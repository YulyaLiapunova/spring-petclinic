package org.springframework.samples.petclinic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.owner.OwnerRepository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class DeletePetEndpointTests {

	@LocalServerPort
	private int port;

	@Autowired
	private RestTemplateBuilder restTemplateBuilder;

	@Autowired
	private OwnerRepository ownerRepository;

	private RestTemplate restTemplate;
	private HttpEntity<Void> entity;


	@BeforeEach
	void setUp() {
		restTemplate = restTemplateBuilder.rootUri("http://localhost:" + port).build();

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "valid-token");
		entity = new HttpEntity<>(headers);

		// Initialize test data
		Owner owner = new Owner();
		owner.setFirstName("John");
		owner.setLastName("Doe");
		owner.setAddress("123 Main Street");
		owner.setCity("Anytown");
		owner.setTelephone("1234567890");

		Pet pet = new Pet();
		pet.setName("Fido");
		pet.setBirthDate(LocalDate.of(2020, 1, 1));
		PetType petType = new PetType();
		petType.setId(1);
		petType.setName("dog");
		pet.setType(petType);
		owner.addPet(pet);

		ownerRepository.save(owner);
	}

	@Test
	void testDeletePetSuccess() {
		// Arrange
		Owner owner = ownerRepository.findAll().get(0);
		Pet pet = owner.getPets().get(0);
		String ownerId = String.valueOf(owner.getId());
		String petId = String.valueOf(pet.getId());
		String url = "/owners/" + ownerId + "/pets/" + petId + "/delete";

		// Act
		ResponseEntity<Void> response = restTemplate.postForEntity(url, entity, Void.class);

		// Assert
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(ownerRepository.findById(owner.getId()).get().getPets().contains(pet)).isFalse();
	}

	@Test
	void testDeletePetNotFound() {
		// Arrange
		String ownerId = "1"; // Valid owner ID
		String petId = "999"; // Non-existent pet ID
		String url = "/owners/" + ownerId + "/pets/" + petId + "/delete";

		// Act
		assertThatThrownBy(() -> restTemplate.postForEntity(url, entity, String.class))
			.isInstanceOf(HttpClientErrorException.NotFound.class)
			.hasMessageContaining("Pet with ID " + petId + " not found");
	}

	@Test
	void testDeletePetUnauthorized() {
		// Arrange
		String ownerId = "1";
		String petId = "1";
		String url = "http://localhost:" + port + "/owners/" + ownerId + "/pets/" + petId + "/delete";

		// Act
		assertThatThrownBy(() -> restTemplate.postForEntity(url, null, String.class))
			.isInstanceOf(HttpClientErrorException.Unauthorized.class);
	}

	@Test
	void testDeletePetInvalidOwnerId() {
		// Arrange
		String ownerId = "99999";
		String petId = "1";
		String url = "/owners/" + ownerId + "/pets/" + petId + "/delete";

		// Act
		assertThatThrownBy(() -> restTemplate.postForEntity(url, null, String.class))
			.isInstanceOf(HttpServerErrorException.InternalServerError.class);
	}
}
