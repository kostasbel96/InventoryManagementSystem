package gr.aueb.cf.inventorymanagementsystem.repository;

import gr.aueb.cf.inventorymanagementsystem.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long>, JpaSpecificationExecutor<Supplier> {
    Optional<Supplier> findByName(String name);
    Optional<Supplier> findByPhoneNumber(String phoneNumber);
    Optional<Supplier> findByEmail(String email);
}
