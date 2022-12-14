package io.bootify.saven.rest;

import io.bootify.saven.model.BillDTO;
import io.bootify.saven.service.BillService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api/bills", produces = MediaType.APPLICATION_JSON_VALUE)
public class BillResource {

    private final BillService billService;

    public BillResource(final BillService billService) {
        this.billService = billService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('read:bills')")
    public ResponseEntity<List<BillDTO>> getAllBills() {
        return ResponseEntity.ok(billService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('read:bill')")
    public ResponseEntity<BillDTO> getBill(@PathVariable final UUID id) {
        return ResponseEntity.ok(billService.get(id));
    }

    @GetMapping("getPastBills/{user_id}/{numDays}")
    public ResponseEntity<List<BillDTO>> getUserBills(@PathVariable final UUID user_id, @PathVariable final int numDays) {
        return ResponseEntity.ok(billService.getUserBills(user_id, numDays));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('upload:bill')")
    @ApiResponse(responseCode = "201")
    public ResponseEntity<UUID> createBill(@RequestBody @Valid final BillDTO billDTO) {
        return new ResponseEntity<>(billService.create(billDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('update:bill')")
    public ResponseEntity<Void> updateBill(@PathVariable final UUID id,
            @RequestBody @Valid final BillDTO billDTO) {
        billService.update(id, billDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('delete:bill')")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteBill(@PathVariable final UUID id) {
        billService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
