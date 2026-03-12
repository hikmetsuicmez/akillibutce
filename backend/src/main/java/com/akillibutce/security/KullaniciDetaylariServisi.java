package com.akillibutce.security;

import com.akillibutce.entity.Kullanici;
import com.akillibutce.repository.KullaniciRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KullaniciDetaylariServisi implements UserDetailsService {

    private final KullaniciRepository kullaniciRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String eposta) throws UsernameNotFoundException {
        Kullanici kullanici = kullaniciRepository.findByEposta(eposta)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Kullanici bulunamadi: " + eposta));

        return new User(
                kullanici.getEposta(),
                kullanici.getSifre(),
                List.of(new SimpleGrantedAuthority(kullanici.getRol().name()))
        );
    }
}
