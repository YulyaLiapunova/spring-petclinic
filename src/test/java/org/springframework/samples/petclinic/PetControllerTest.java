```Java
import org.testng.annotations.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;

class PetControllerTest {

    @Test
    public void testDeletePetPositive() {
        Owner owner = new Owner("John", "Doe");
        Pet pet = new Pet("Buddy", LocalDate.of(2019, 1, 1), PetType.DOG);
        Visit visit = new Visit();
        visit.setDescription("Visit description");
        List<Visit> visits = new ArrayList<>();
        visits.add(visit);
        pet.setVisits(visits);
        owner.setPets(Arrays.asList(pet));
        
        String response = PetController.deletePet(owner, pet);
        
        assertEquals("Successfully deleted pet", response);
    }
    
    @Test
    public void testDeletePetNegative() {
        Owner owner = new Owner("John", "Doe");
        Pet pet = new Pet("Buddy", LocalDate.of(2019, 1, 1), PetType.DOG);
        
        String response = PetController.deletePet(owner, pet);
        
        assertEquals("Error occurred during delete operation", response);
    }
    
    @Test
    public void testDeletePetEdgeCases() {
        Owner owner = new Owner("John", "Doe");
        Pet pet = new Pet("Buddy", LocalDate.of(2019, 1, 1), PetType.DOG);
        
        String response = PetController.deletePet(owner, pet);
        
        assertEquals("Error occurred during delete operation", response);
    }
}
```
