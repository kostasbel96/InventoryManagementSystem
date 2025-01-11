package gr.aueb.cf.inventorymanagementsystem.repository;

import gr.aueb.cf.inventorymanagementsystem.dto.OrderReadOnlyDTO;
import gr.aueb.cf.inventorymanagementsystem.model.Order;
import gr.aueb.cf.inventorymanagementsystem.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

}
