package org.prgrms.voucherapplication.domain.voucher.repository;

import org.prgrms.voucherapplication.domain.voucher.entity.Voucher;
import org.prgrms.voucherapplication.domain.voucher.entity.VoucherType;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Profile("dev")
@Repository
public class VoucherMemoryRepository implements VoucherRepository{

    private final Map<UUID, Voucher> storage = new ConcurrentHashMap<>();

    @Override
    public Voucher save(Voucher voucher) {
        storage.put(voucher.getVoucherId(), voucher);
        return voucher;
    }

    @Override
    public List<Voucher> findAll() {
        List<Voucher> voucherList = new ArrayList<>();
        storage.forEach((uuid, voucher) -> voucherList.add(voucher));
        return voucherList;
    }

    @Override
    public int deleteAll() {
        int size = storage.size();
        storage.clear();
        return size;
    }

    @Override
    public Optional<Voucher> findById(UUID voucherId) {
        return Optional.ofNullable(storage.get(voucherId));
    }

    @Override
    public void deleteById(UUID voucherId) {
        storage.remove(voucherId);
    }

    @Override
    public List<Voucher> findByType(VoucherType type) {
        return List.of();
    }

    @Override
    public List<Voucher> findByTypeAndCreatedAt(VoucherType type, LocalDateTime createdAt) {
        return null;
    }

    @Override
    public List<Voucher> findByCreatedAtAfter(LocalDateTime createdAt) {
        return null;
    }
}
