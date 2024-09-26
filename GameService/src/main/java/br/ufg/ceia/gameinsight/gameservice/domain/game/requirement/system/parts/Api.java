package br.ufg.ceia.gameinsight.gameservice.domain.game.requirement.system.parts;

import jakarta.persistence.*;
import org.springframework.stereotype.Repository;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * Represents the graphical application programming interface (API) required by a game.
 * <p>
 * Such as DirectX, OpenGL, Vulkan, etc.
 */
@Entity
@Table(name = "api")
public class Api implements Serializable {

    /**
     * The serial version UID.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The unique identifier of the API.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The name of the API.
     */
    private String name;

    /**
     * The Version of the API.
     */
    private String version;

    public Api() {
    }

    public Api(Long id, String name, String version) {
        this.id = id;
        this.name = name;
        this.version = version;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "Api{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", version='" + version + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Api api)) return false;

        if (!Objects.equals(id, api.id)) return false;
        if (!Objects.equals(name, api.name)) return false;
        return Objects.equals(version, api.version);
    }

}
