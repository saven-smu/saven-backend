package io.bootify.saven.service;

import io.bootify.saven.domain.Bill;
import io.bootify.saven.domain.User;
import io.bootify.saven.model.BillDTO;
import io.bootify.saven.repos.BillRepository;
import io.bootify.saven.repos.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
public class BillService {

    private final BillRepository billRepository;
    private final UserRepository userRepository;

    public BillService(final BillRepository billRepository, final UserRepository userRepository) {
        this.billRepository = billRepository;
        this.userRepository = userRepository;
    }

    public List<BillDTO> findAll() {
        return billRepository.findAll(Sort.by("id"))
                .stream()
                .map(bill -> mapToDTO(bill, new BillDTO()))
                .collect(Collectors.toList());
    }

    public BillDTO get(final UUID id) {
        return billRepository.findById(id)
                .map(bill -> mapToDTO(bill, new BillDTO()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public UUID create(final BillDTO billDTO) {
        final Bill bill = new Bill();
        mapToEntity(billDTO, bill);
        return billRepository.save(bill).getId();
    }

    public void update(final UUID id, final BillDTO billDTO) {
        final Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        mapToEntity(billDTO, bill);
        billRepository.save(bill);
    }

    public void delete(final UUID id) {
        billRepository.deleteById(id);
    }

    public List<BillDTO> getUserBills(final UUID user_id, final int numDays) {
        int TIMEZONE_OFFSET = 8;
        return billRepository.findByUser_IdAndStoredDateTimeAfter(user_id, LocalDateTime.now().plusHours(TIMEZONE_OFFSET).minusDays(numDays))
                .stream()
                .map(bill -> mapToDTO(bill, new BillDTO()))
                .collect(Collectors.toList());
    }

    private BillDTO mapToDTO(final Bill bill, final BillDTO billDTO) {
        billDTO.setId(bill.getId());
        billDTO.setElectricityUsed(bill.getElectricityUsed());
        billDTO.setWaterUsed(bill.getWaterUsed());
        billDTO.setGasUsed(bill.getGasUsed());
        billDTO.setElectricityCost(bill.getElectricityCost());
        billDTO.setWaterCost(bill.getWaterCost());
        billDTO.setGasCost(bill.getGasCost());
        billDTO.setTotalCost(bill.getTotalCost());
        billDTO.setStoredDateTime(bill.getStoredDateTime());
        billDTO.setUser(bill.getUser() == null ? null : bill.getUser().getId());
        return billDTO;
    }

    private Bill mapToEntity(final BillDTO billDTO, final Bill bill) {
        bill.setElectricityUsed(billDTO.getElectricityUsed());
        bill.setWaterUsed(billDTO.getWaterUsed());
        bill.setGasUsed(billDTO.getGasUsed());
        bill.setElectricityCost(billDTO.getElectricityCost());
        bill.setWaterCost(billDTO.getWaterCost());
        bill.setGasCost(billDTO.getGasCost());
        bill.setTotalCost(billDTO.getTotalCost());
        bill.setStoredDateTime(billDTO.getStoredDateTime());
        final User user = billDTO.getUser() == null ? null : userRepository.findById(billDTO.getUser())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found"));
        bill.setUser(user);
        return bill;
    }

}
