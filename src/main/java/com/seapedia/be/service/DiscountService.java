package com.seapedia.be.service;

import com.seapedia.be.dto.PromoRequest;
import com.seapedia.be.dto.PromoResponse;
import com.seapedia.be.dto.VoucherRequest;
import com.seapedia.be.dto.VoucherResponse;
import com.seapedia.be.model.Promo;
import com.seapedia.be.model.Voucher;
import com.seapedia.be.repository.PromoRepository;
import com.seapedia.be.repository.VoucherRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DiscountService {
    private final VoucherRepository voucherRepository;
    private final PromoRepository promoRepository;

    public DiscountService(VoucherRepository voucherRepository, PromoRepository promoRepository) {
        this.voucherRepository = voucherRepository;
        this.promoRepository = promoRepository;
    }

    @Transactional
    public VoucherResponse createVoucher(VoucherRequest request) {
        if (voucherRepository.findByCodeIgnoreCase(request.code()).isPresent() ||
            promoRepository.findByCodeIgnoreCase(request.code()).isPresent()) {
            throw new IllegalArgumentException("Discount code already exists");
        }
        Voucher voucher = Voucher.builder()
                .code(request.code().toUpperCase())
                .discountAmount(request.discountAmount())
                .expiryDate(request.expiryDate())
                .remainingUsage(request.remainingUsage())
                .build();
        voucher = voucherRepository.save(voucher);
        return mapToVoucherResponse(voucher);
    }

    @Transactional
    public PromoResponse createPromo(PromoRequest request) {
        if (promoRepository.findByCodeIgnoreCase(request.code()).isPresent() ||
            voucherRepository.findByCodeIgnoreCase(request.code()).isPresent()) {
            throw new IllegalArgumentException("Discount code already exists");
        }
        Promo promo = Promo.builder()
                .code(request.code().toUpperCase())
                .discountPercent(request.discountPercent())
                .expiryDate(request.expiryDate())
                .build();
        promo = promoRepository.save(promo);
        return mapToPromoResponse(promo);
    }

    @Transactional(readOnly = true)
    public List<VoucherResponse> getVouchers() {
        return voucherRepository.findAll().stream().map(this::mapToVoucherResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<PromoResponse> getPromos() {
        return promoRepository.findAll().stream().map(this::mapToPromoResponse).toList();
    }

    private VoucherResponse mapToVoucherResponse(Voucher voucher) {
        return new VoucherResponse(
                voucher.getId(),
                voucher.getCode(),
                voucher.getDiscountAmount(),
                voucher.getExpiryDate(),
                voucher.getRemainingUsage()
        );
    }

    private PromoResponse mapToPromoResponse(Promo promo) {
        return new PromoResponse(
                promo.getId(),
                promo.getCode(),
                promo.getDiscountPercent(),
                promo.getExpiryDate()
        );
    }
}
