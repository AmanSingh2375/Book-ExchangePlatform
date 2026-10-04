
package bookexchange.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import bookexchange.model.ExchangeRequest;

public interface ExchangeRequestRepository
        extends JpaRepository<ExchangeRequest, Integer> {
}