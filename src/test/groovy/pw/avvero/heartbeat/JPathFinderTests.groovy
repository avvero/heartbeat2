package pw.avvero.heartbeat

import spock.lang.Specification
import spock.lang.Unroll

class JPathFinderTests extends Specification {

    @Unroll
    def "Value is #value in map #map by path #path"() {
        expect:
        new JPathFinder(path as String[]).search(map) == value
        where:
        map        | path  | value
        null       | null  | null
        [:]        | null  | null
        ["1": "a"] | ["1"] | "a"
    }

}
