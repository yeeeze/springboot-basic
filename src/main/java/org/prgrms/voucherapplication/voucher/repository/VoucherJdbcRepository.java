package org.prgrms.voucherapplication.voucher.repository;

import org.prgrms.voucherapplication.customer.NothingInsertException;
import org.prgrms.voucherapplication.voucher.entity.Voucher;
import org.prgrms.voucherapplication.voucher.entity.VoucherType;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Repository
@Profile("prod")
public class VoucherJdbcRepository implements VoucherRepository {

    private static final String NOTHING_INSERT = "Nothing was inserted";

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public VoucherJdbcRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    private static final RowMapper<Voucher> voucherRowMapper = (resultSet, i) -> {
        UUID voucherId = toUUID(resultSet.getBytes("voucher_id"));
        int discount = resultSet.getInt("discount");
        String voucherTypeName = resultSet.getString("voucher_type");
        LocalDateTime createdAt = resultSet.getTimestamp("created_at").toLocalDateTime();

        VoucherType voucherType = VoucherType.of(voucherTypeName);
        return voucherType.createVoucher(voucherId, discount, createdAt);
    };

    static UUID toUUID(byte[] bytes) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        return new UUID(byteBuffer.getLong(), byteBuffer.getLong());
    }

    @Override
    public Voucher save(Voucher voucher) {
        Map<String, Object> paraMap = new HashMap<>();
        paraMap.put("voucherId", voucher.getUuid().toString().getBytes());
        paraMap.put("discount", voucher.getDiscount());
        paraMap.put("voucherType", voucher.getVoucherType().name());
        paraMap.put("createdAt", Timestamp.valueOf(voucher.getCreatedAt()));

        int update = namedParameterJdbcTemplate.update("INSERT INTO vouchers(voucher_id, discount, voucher_type, created_at) VALUES (UUID_TO_BIN(:voucherId), :discount, :voucherType, :createdAt)",
                paraMap);
        if (update != 1) {
            throw new NothingInsertException(NOTHING_INSERT);
        }

        return voucher;
    }

    @Override
    public List<Voucher> findAll() {
        return namedParameterJdbcTemplate.query("select * from vouchers", voucherRowMapper);
    }

    @Override
    public int deleteAll() {
        return namedParameterJdbcTemplate.update("DELETE FROM vouchers", Collections.emptyMap());
    }
}