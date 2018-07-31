package it.geny.goosegame

import spock.lang.Specification

class GooseGameTDD extends Specification {

    def "Add a new player"() {
        setup: "If there is no participant"
          def gooseDSL = GooseDSL.instance
        when: "the user writes: \"add player Pippo\""
          def result = gooseDSL.executeCommandGame {
              addPlayer "Pippo"
          }
        then: "the system responds: \"players: Pippo\""
          logResults(result)
          result == "players: Pippo"
        when: "the user writes: \"add player Pluto\""
          result = gooseDSL.executeCommandGame {
              addPlayer "Pluto"
          }
        then: "the system responds: \"players: Pippo, Pluto\""
          logResults(result)
          result == "players: Pippo, Pluto"
    }

    def "Try to add a duplicate player"() {
        setup: "If there is already a participant \"Pippo\""
          def gooseDSL = GooseDSL.instance
          gooseDSL.executeCommandGame {
              addPlayer "Pippo"
          }
        when: "the user writes: \"add player Pippo\""
          def result = gooseDSL.executeCommandGame {
              addPlayer "Pippo"
          }
        then: "the system responds: \"Pippo: already existing player\""
          logResults(result)
          result == "Pippo: already existing player"
    }

    def "Start game with Pippo and Pluto"() {
        setup: "If there are two participants \"Pippo\" and \"Pluto\" on space \"Start\""
          def gooseDSL = GooseDSL.instance
          gooseDSL.executeCommandGame {
              addPlayer "Pippo"
              addPlayer "Pluto"
          }
        when: "the user writes: \"move Pippo 4, 2\""
          def result = gooseDSL.executeCommandGame {
              move "Pippo 4, 2"
          }
        then: "the system responds: \"Pippo rolls 4, 2. Pippo moves from Start to 6\""
          logResults(result)
          result == "Pippo rolls 4, 2. Pippo moves from Start to The Bridge. Pippo jumps to 12"

        when: "the user writes: \"move Pluto 2, 2\""
          result = gooseDSL.executeCommandGame {
              move "Pluto 2, 2"
          }
        then: "the system responds: \"Pluto rolls 2, 2. Pluto moves from Start to 4\""
          logResults(result)
          result == "Pluto rolls 2, 2. Pluto moves from Start to 4"
        when: "the user writes: \"move Pippo 2, 3\""
          result = gooseDSL.executeCommandGame {
              move "Pippo 2, 3"
          }
        then: "the system responds: \"Pippo rolls 2, 3. Pippo moves from 12 to 17\""
          logResults(result)
          result == "Pippo rolls 2, 3. Pippo moves from 12 to 17"
    }

    def "Victory scenario"() {
        setup: "If there is one participant \"Pippo\" on space \"60\""
          def gooseDSL = GooseDSL.instance
          def result = gooseDSL.executeCommandGame {
              addPlayer "Pippo"
              cheaterOnlyForTest "Pippo 60"
          }
          assert result == "Cheater! Current space of Pippo is 60"
        when: "the user writes: \"move Pippo 1, 2\""
          result = gooseDSL.executeCommandGame {
              move "Pippo 1, 2"
          }
        then: "the system responds: \"Pippo rolls 1, 2. Pippo moves from 60 to 63. Pippo Wins!!\""
          logResults(result)
          result == "Pippo rolls 1, 2. Pippo moves from 60 to 63. Pippo Wins!!"
    }

    def "If there is one participant \"Pippo\" on space \"60\" "() {
        setup: "If there is one participant \"Pippo\" on space \"60\""
          def gooseDSL = GooseDSL.instance
          def result = gooseDSL.executeCommandGame {
              addPlayer "Pippo"
              cheaterOnlyForTest "Pippo 60"
          }
          assert result == "Cheater! Current space of Pippo is 60"

        when: "the user writes: \"move Pippo 3, 2\""
          result = gooseDSL.executeCommandGame {
              move "Pippo 3, 2"
          }
        then: "the system responds: \"Pippo rolls 3, 2. Pippo moves from 60 to 63. Pippo bounces! Pippo returns to 61\""
          logResults(result)
          result == "Pippo rolls 3, 2. Pippo moves from 60 to 63. Pippo bounces! Pippo returns to 61"
    }

    def "The games throws the dices"() {
        setup: "If there is one participant \"Pippo\" on space \"4\""
          def gooseDSL = GooseDSL.instance
          def result = gooseDSL.executeCommandGame {
              addPlayer "Pippo"
              cheaterOnlyForTest "Pippo 4"
          }
        and: "assuming that the dice get 1 and 2"
          gooseDSL.executeCommandGame {
              forceDiceRoll "1, 2"
          }
        when: "when the user writes: \"move Pippo\""
          result = gooseDSL.executeCommandGame {
              move "Pippo"
          }
        then: "the system responds: \"Pippo rolls 1, 2. Pippo moves from 4 to 7\""
          logResults(result)
          result == "Pippo rolls 1, 2. Pippo moves from 4 to 7"
    }

    def "The games throws the dices (No assuming dices values)"() {
        setup: "If there is one participant \"Pippo\" on space \"4\""
          def gooseDSL = GooseDSL.instance
          def result = gooseDSL.executeCommandGame {
              addPlayer "Pippo"
              cheaterOnlyForTest "Pippo 4"
          }
        when: "when the user writes: \"move Pippo\""
          result = gooseDSL.executeCommandGame {
              move "Pippo"
          }
        then: "the system responds: \"Pippo rolls x, y Pippo moves from x to y\""
          logResults(result)
          result ==~ /(?s).*Pippo rolls \d, \d. Pippo moves from \d to \d.*/
    }


    def "Space \"6\" is \"The Bridge\""() {
        setup: "If there is one participant \"Pippo\" on space \"4\""
          def gooseDSL = GooseDSL.instance
          gooseDSL.executeCommandGame {
              addPlayer "Pippo"
              cheaterOnlyForTest "Pippo 4"
          }
        and: "assuming that the dice get 1 and 1"
          gooseDSL.executeCommandGame {
              forceDiceRoll "1, 1"
          }
        when: "when the user writes: \"move Pippo\""
          def result = gooseDSL.executeCommandGame {
              move "Pippo"
          }
        then: "the system responds: \"Pippo rolls 1, 1. Pippo moves from 4 to The Bridge. Pippo jumps to 12\""
          logResults(result)
          result == "Pippo rolls 1, 1. Pippo moves from 4 to The Bridge. Pippo jumps to 12"
    }

    def "If you land on \"The Goose\", move again"() {
        setup: "If there is one participant \"Pippo\" on space \"3\""
          def gooseDSL = GooseDSL.instance
          gooseDSL.executeCommandGame {
              addPlayer "Pippo"
              cheaterOnlyForTest "Pippo 3"
          }
        and: "assuming that the dice get 1 and 1"
          gooseDSL.executeCommandGame {
              forceDiceRoll "1, 1"
          }
        when: "when the user writes: \"move Pippo\""
          def result = gooseDSL.executeCommandGame {
              move "Pippo"
          }
        then: "the system responds: \"Pippo rolls 1, 1. Pippo moves from 3 to 5, The Goose. Pippo moves again and goes to 7\""
          logResults(result)
          result == "Pippo rolls 1, 1. Pippo moves from 3 to 5, The Goose. Pippo moves again and goes to 7"
    }

    def "If you land on \"The Goose\", move again (multiple jumps)"() {
        setup: "If there is one participant \"Pippo\" on space \"10\""
          def gooseDSL = GooseDSL.instance
          gooseDSL.executeCommandGame {
              addPlayer "Pippo"
              cheaterOnlyForTest "Pippo 10"
          }
        and: "assuming that the dice get 2 and 2"
          gooseDSL.executeCommandGame {
              forceDiceRoll "2, 2"
          }
        when: "when the user writes: \"move Pippo\""
          def result = gooseDSL.executeCommandGame {
              move "Pippo"
          }
        then: "the system responds: \"Pippo rolls 2, 2. Pippo moves from 10 to 14, The Goose. Pippo moves again and goes to 18, The Goose. Pippo moves again and goes to 22\""
          logResults(result)
          result == "Pippo rolls 2, 2. Pippo moves from 10 to 14, The Goose. Pippo moves again and goes to 18, The Goose. Pippo moves again and goes to 22"
    }

    def "Prank!"() {
        setup: "If there are two participants \"Pippo\" and \"Pluto\" respectively on spaces \"15\" and \"17\""
          def gooseDSL = GooseDSL.instance
          gooseDSL.executeCommandGame {
              addPlayer "Pippo"
              addPlayer "Pluto"
              cheaterOnlyForTest "Pippo 15"
              cheaterOnlyForTest "Pluto 17"
          }
        and: "assuming that the dice get 1 and 1"
          gooseDSL.executeCommandGame {
              forceDiceRoll "1, 1"
          }
        when: "when the user writes: \"move Pippo\""
          def result = gooseDSL.executeCommandGame {
              move "Pippo"
          }
        then: "the system responds: \"Pippo rolls 1, 1. Pippo moves from 15 to 17. On 17 there is Pluto, who returns to 15\""
          logResults(result)
          result == "Pippo rolls 1, 1. Pippo moves from 15 to 17. On 17 there is Pluto, who returns to 15"
    }


    protected logResults(result) {
        println result
        reportInfo result
        true
    }
}
