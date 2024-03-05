package aws.workflowapp.redshift;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@ComponentScan(basePackages = {"aws.workflowapp.redshift"})
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("api/items")
public class MainController {
    private final WorkItemRepository repository;

    @Autowired
    MainController(
        WorkItemRepository repository) {
        this.repository = repository;
    }

    @GetMapping("" )
    public List<Scout> getItems(@RequestParam(required=false) String status) {
        Iterable<Scout> result;
        if (status != null && status.compareTo("Approved")==0)
            result = repository.getData("Approved");
        else if (status != null && status.compareTo("Research")==0)
            result = repository.getData("Research");
        else
            result = repository.getData("Draft");

        return StreamSupport.stream(result.spliterator(), false)
            .collect(Collectors.toUnmodifiableList());
    }

    @GetMapping("/count" )
    public String getCount() {
        return repository.countItems();
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateItem(@RequestBody UpdateItemRequest payload) {
        if (payload.getId() == null || payload.getStatus() == null) {
            return ResponseEntity.badRequest().build(); // 400 Bad Request
        }

        String result = repository.modItem(payload.getId(), payload.getStatus());
        if (result.equals("success")) {
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            return ResponseEntity.ok(result); // 200 OK with additional information
        }
    }

    @PostMapping("")
    public ResponseEntity<String> addItem(@RequestBody Scout scout) {
        String IdVal = repository.popTable(scout);
        return ResponseEntity.ok(IdVal);
    }

    @GetMapping("/{itemId}")
    public String getItemById(@PathVariable Long itemId) {

        List<Scout> result = repository.getDataById(itemId);

        try {
            // Create an ObjectMapper.
            ObjectMapper objectMapper = new ObjectMapper();

            // Convert the List of a Scout object to JSON.
            String json = objectMapper.writeValueAsString(result);
            if (json.startsWith("[") && json.endsWith("]")) {
                json = json.substring(1, json.length() - 1);
            }

            // Return the JSON string
            return json;

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @PutMapping("/progress")
    public ResponseEntity<String> updateResearch(@RequestBody UpdateItemRequest payload) {
        if (payload.getId() == null ) {
            return ResponseEntity.badRequest().build(); // 400 Bad Request
        }

        // First must look up the item score.
        int itemScore = repository.lookUpItemScore(payload.getId());
        if (itemScore >= 80) {
            String result = repository.modItem(payload.getId(),"InProgress");
            return ResponseEntity.ok(result); // 200 OK with additional information
         } else {
            return ResponseEntity.badRequest().body("The item score does not meet the requirements"); // 400 Bad Request
        }
    }
}
