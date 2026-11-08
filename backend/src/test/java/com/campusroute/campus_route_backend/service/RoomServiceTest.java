package com.campusroute.campus_route_backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.campusroute.campus_route_backend.dto.RoomDto;

// Full context: GraphDataLoader fills the H2 database with the 27 rooms at startup
@SpringBootTest
class RoomServiceTest {

    @Autowired RoomService service;

    @Test
    void ignoresCaseAndSpacesOnCode() {
        assertThat(service.search("b204")).extracting(RoomDto::code).startsWith("B204");
        assertThat(service.search("  B 2 0 4 ")).extracting(RoomDto::code).startsWith("B204");
    }

    @Test
    void findsByNameAndByType() {
        assertThat(service.search("TURING")).extracting(RoomDto::code).containsExactly("B001");
        assertThat(service.search("amphi")).extracting(RoomDto::code).contains("B001");
        assertThat(service.search("salle de td")).isNotEmpty(); // spaces ignored inside the query too
    }

    @Test
    void blankQueryGivesEmptyList() {
        assertThat(service.search("")).isEmpty();
        assertThat(service.search("   ")).isEmpty();
        assertThat(service.search(null)).isEmpty();
    }

    @Test
    void capsAtEightResultsSortedByCode() {
        List<String> codes = service.search("b").stream().map(RoomDto::code).toList(); // "b" is in all 27 codes
        assertThat(codes).hasSize(8).isSorted();
        assertThat(codes.get(0)).isEqualTo("B001");
    }

    @Test
    void wildcardsAreSearchedLiterally() {
        assertThat(service.search("%")).isEmpty();
        assertThat(service.search("_")).isEmpty();
    }

    @Test
    void findByCodeTrimsAndUppercases() {
        RoomDto room = service.findByCode(" b204 ");
        assertThat(room.code()).isEqualTo("B204");
        assertThat(room.floor()).isEqualTo(2);
        assertThat(room.capacity()).isEqualTo(30);
        assertThat(room.equipment()).containsExactlyInAnyOrder("Tableau", "Vidéoprojecteur");
    }

    @Test
    void findByCodeThrowsWhenUnknown() {
        assertThatThrownBy(() -> service.findByCode("B999"))
                .isInstanceOf(RoomNotFoundException.class)
                .hasMessage("Salle B999 introuvable");
    }
}
