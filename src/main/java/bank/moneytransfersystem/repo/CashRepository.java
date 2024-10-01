package bank.moneytransfersystem.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import bank.moneytransfersystem.entities.Cash;

import java.util.List;

@Repository
public interface CashRepository extends JpaRepository<Cash, Long> {
//    boolean existsByCashName(String cashName);

//    List<Cash> findByCashName(String name);
}

