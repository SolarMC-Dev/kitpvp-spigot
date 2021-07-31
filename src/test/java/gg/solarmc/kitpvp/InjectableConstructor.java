/*
 * kitpvp
 * Copyright © 2021 SolarMC Developers
 *
 * kitpvp is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * kitpvp is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with kitpvp. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU Affero General Public License.
 */

package gg.solarmc.kitpvp;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import jakarta.inject.Inject;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A constructor annotated with {@code Inject}
 *
 */
public final class InjectableConstructor {

    private final Class<?> subject;

    public InjectableConstructor(Class<?> subject) {
        this.subject = subject;
    }

    /**
     * Finds the injectable constructor
     *
     * @return the injectable constructor
     */
    public Constructor<?> findConstructor() {
        return Arrays.stream(subject.getConstructors())
                .filter((ctor) -> ctor.isAnnotationPresent(Inject.class))
                .findAny()
                .orElseThrow();
    }

    /**
     * Verifies that the injectable constructor contains at least the given parameters
     *
     * @param parameters the parameter types the constructor should at least have
     */
    public void verifyParametersContain(Set<Class<?>> parameters) {
        Set<Class<?>> actualParameters = Set.of(findConstructor().getParameterTypes());
        Set<Class<?>> leftovers = new HashSet<>(parameters);
        leftovers.removeAll(actualParameters);
        assertTrue(leftovers.isEmpty(),
                () -> "Expected parameters of " + subject.getName() + " constructor to contain " + leftovers);
    }

    public void verifyParametersContainSubclassesOf(Class<?> superclass) {
        Set<Class<?>> scannedClasses;
        try (ScanResult scan = new ClassGraph()
                .enableClassInfo()
                .acceptModules(subject.getModule().getName())
                .scan()) {
            String superclassName = superclass.getName();
            ClassInfoList classInfoList = (superclass.isInterface()) ?
                    scan.getClassesImplementing(superclassName) : scan.getSubclasses(superclassName);
            scannedClasses = classInfoList
                    .getNames()
                    .stream().map((name) -> {
                        try {
                            return Class.forName(name);
                        } catch (ClassNotFoundException ex) {
                            throw new IllegalStateException(ex);
                        }
                    }).collect(Collectors.toUnmodifiableSet());
        }
        verifyParametersContain(scannedClasses);
    }
}
