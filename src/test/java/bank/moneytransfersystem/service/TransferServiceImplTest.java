package bank.moneytransfersystem.service;

import bank.moneytransfersystem.entities.Cash;
import bank.moneytransfersystem.entities.Transfer;
import bank.moneytransfersystem.enums.Currency;
import bank.moneytransfersystem.enums.Status;
import bank.moneytransfersystem.exception.IllegalArgumentException;
import bank.moneytransfersystem.exception.NotFoundException;
import bank.moneytransfersystem.repo.CashRepository;
import bank.moneytransfersystem.repo.TransferRepository;
import bank.moneytransfersystem.service.impl.TransferServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferServiceImplTest {

    @Mock
    private TransferRepository transferRepository;

    @Mock
    private CashService cashService;

    @Mock
    private CashRepository cashRepository;

    @InjectMocks
    private TransferServiceImpl transferService;

    public void TransferServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createTransfer_ShouldThrowException_WhenFromCashIsNull() {
        Transfer transfer = new Transfer();
        transfer.setToCash(new Cash());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            transferService.createTransfer(1L, transfer);
        });
        assertEquals("Cash with ID 1 not found", exception.getMessage());
    }

    @Test
    void createTransfer_ShouldThrowException_WhenCurrencyIsNull() {
        Transfer transfer = new Transfer();
        transfer.setFromCash(new Cash());
        transfer.setToCash(new Cash());
        transfer.setCurrency(null);

        when(cashRepository.findById(anyLong())).thenReturn(Optional.of(new Cash()));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transferService.createTransfer(1L, transfer);
        });

        assertEquals("Currency must not be null", exception.getMessage());
    }

    @Test
    void createTransfer_ShouldCreateTransfer_WhenValid() throws NotFoundException, IllegalArgumentException {
        Cash fromCash = new Cash();
        fromCash.setId(1L);
        fromCash.setBalance(2000.0);

        Cash toCash = new Cash();
        toCash.setId(2L);
        toCash.setBalance(500.0);

        Transfer transfer = new Transfer();
        transfer.setFromCash(fromCash);
        transfer.setToCash(toCash);
        transfer.setAmount(1000.0);
        transfer.setCurrency(Currency.valueOf("USD"));

        when(cashRepository.findById(1L)).thenReturn(Optional.of(new Cash()));
        when(cashService.getCashById(1L)).thenReturn(fromCash);
        when(cashService.getCashById(2L)).thenReturn(toCash);

        String uniqueCode = transferService.createTransfer(1L, transfer);

        assertNotNull(uniqueCode);
        assertEquals(1000.0, fromCash.getBalance());
        assertEquals(1500.0, toCash.getBalance());

        verify(transferRepository, times(1)).save(transfer);
    }

    @Test
    void processTransfer_ShouldThrowException_WhenBalanceIsInsufficient() {
        Transfer transfer = new Transfer();
        Cash fromCash = new Cash();
        fromCash.setBalance(500);
        transfer.setFromCash(fromCash);
        transfer.setToCash(new Cash());
        transfer.setAmount(1000);

        when(transferRepository.findByCode(anyString())).thenReturn(Optional.of(transfer));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transferService.processTransfer("someCode");
        });

        assertEquals("Insufficient balance in source cash", exception.getMessage());
    }

    @Test
    void findAllTransfers_ShouldReturnListOfTransfers() {
        List<Transfer> transfers = Arrays.asList(new Transfer(), new Transfer());

        when(transferRepository.findAllTransfers(anyLong())).thenReturn(transfers);

        List<Transfer> result = transferService.findAllTransfers(1L);

        assertEquals(2, result.size());
        verify(transferRepository, times(1)).findAllTransfers(1L);
    }

    @Test
    void getTransferByCode_ShouldReturnTransfer_WhenCodeExists() throws NotFoundException {
        Transfer transfer = new Transfer();
        transfer.setCode("uniqueCode");

        when(transferRepository.findByCode("uniqueCode")).thenReturn(Optional.of(transfer));

        Transfer result = transferService.getTransferByCode("uniqueCode");

        assertEquals("uniqueCode", result.getCode());
        verify(transferRepository, times(1)).findByCode("uniqueCode");
    }

    @Test
    void getTransferByCode_ShouldThrowNotFoundException_WhenCodeDoesNotExist() {
        when(transferRepository.findByCode("nonexistentCode")).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            transferService.getTransferByCode("nonexistentCode");
        });

        assertEquals("Transfer with code nonexistentCode not found", exception.getMessage());
    }

    @Test
    void getTransferById_ShouldReturnTransfer_WhenIdExists() throws NotFoundException {
        Transfer transfer = new Transfer();
        transfer.setId(1L);

        when(transferRepository.findById(1L)).thenReturn(Optional.of(transfer));

        Transfer result = transferService.getTransferById(1L);

        assertEquals(1L, result.getId());
        verify(transferRepository, times(1)).findById(1L);
    }

    @Test
    void getTransferById_ShouldThrowNotFoundException_WhenIdDoesNotExist() {
        when(transferRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            transferService.getTransferById(1L);
        });

        assertEquals("Transfer with ID 1 not found", exception.getMessage());
    }

    @Test
    void updateTransferStatus_ShouldUpdateTransfer_WhenTransferExists() throws NotFoundException {
        Transfer transfer = new Transfer();
        transfer.setId(1L);
        transfer.setStatus(Status.CREATED);

        Transfer updatedTransfer = new Transfer();
        updatedTransfer.setId(1L);
        updatedTransfer.setStatus(Status.ISSUED);

        when(transferRepository.findById(1L)).thenReturn(Optional.of(transfer));
        when(transferRepository.save(any(Transfer.class))).thenAnswer(invocation -> {
            Transfer arg = invocation.getArgument(0);
            arg.setStatus(Status.ISSUED);
            return arg;
        });

        Transfer result = transferService.updateTransferStatus(1L, updatedTransfer);

        assertEquals(Status.ISSUED, result.getStatus());

        ArgumentCaptor<Transfer> captor = ArgumentCaptor.forClass(Transfer.class);
        verify(transferRepository, times(1)).save(captor.capture());
        Transfer savedTransfer = captor.getValue();

        assertEquals(1L, savedTransfer.getId());
        assertEquals(Status.ISSUED, savedTransfer.getStatus());
    }


    @Test
    void updateTransferStatus_ShouldThrowNotFoundException_WhenTransferDoesNotExist() {
        Transfer updatedTransfer = new Transfer();
        updatedTransfer.setId(1L);
        updatedTransfer.setStatus(Status.ISSUED);

        when(transferRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            transferService.updateTransferStatus(1L, updatedTransfer);
        });

        assertEquals("Transfer with ID 1 not found", exception.getMessage());
        verify(transferRepository, never()).save(any(Transfer.class));
    }

    @Test
    void deleteTransfer_ShouldThrowNotFoundException_WhenTransferDoesNotExist() {
        when(transferRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            transferService.deleteTransfer(1L);
        });

        assertEquals("Transfer not found", exception.getMessage());
    }
}
