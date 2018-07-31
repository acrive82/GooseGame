package it.geny.goosegame

@Singleton
class GooseDSL {
    def                  GOOSE_POSITION_LIST = [5, 9, 14, 18, 23, 27]
    final static Integer AFTER_JUMP_POSITION = 12
    final static Integer LIMIT_SCORE         = 63
    def                  playersMap          = [:] as Map<String, Integer>
                 int     forcedDice1         = 0
                 int     forcedDice2         = 0

    static def executeCommandGame(closureGameActions) {
        def gameDSL = GooseDSL.instance
        closureGameActions.delegate = gameDSL
        closureGameActions()
    }


    def addPlayer(playerName) {
        if (!playersMap.containsKey(playerName)) {
            playersMap[playerName] = 0
            MessageGameBuilderDSL.build {
                listPlayers playersMap
            }
        } else {
            MessageGameBuilderDSL.build {
                existingPlayer playerName
            }
        }
    }

    def forceDiceRoll(diceRoll) {
        def diceRollList = diceRoll.findAll(/\d+/)
        forcedDice1 = diceRollList[0] as Integer
        forcedDice2 = diceRollList[1] as Integer
    }

    def move(moveAction) {
        def moveActionList = moveAction.findAll(/\w+/)
        def playerName = moveActionList[0] as String
        def dice1 = getDiceValueFromUserOrForcedElseRandomly(forcedDice1, moveActionList, 1)
        def dice2 = getDiceValueFromUserOrForcedElseRandomly(forcedDice2, moveActionList, 2)
        def actualPosition = playersMap[playerName] as Integer
        def nextPosition = (playersMap[playerName] + dice1 + dice2) as Integer
        def prankId = isOccupiedPrankIt(nextPosition)

        MessageGameBuilderDSL.build {

            rolls playerName, dice1, dice2

            def PRANK_CONDITION = prankId >= 0
            def IS_PRANK_CONDITION = GOOSE_POSITION_LIST.contains(nextPosition)
            def OVERCOMING_LIMIT_SCORE_CONDITION = nextPosition > LIMIT_SCORE
            def WIN_CONDITION = nextPosition == LIMIT_SCORE
            def JUMP_CONDITION = nextPosition == 6
            
            switch (true) {
                case PRANK_CONDITION:
                    moves playerName, actualPosition, nextPosition
                    prankIt(delegate, prankId, actualPosition)
                    break
                case IS_PRANK_CONDITION:
                    moves playerName, actualPosition, nextPosition
                    checkIfPlayerOnGoose(delegate, nextPosition, dice1, dice2, playerName)
                    break
                case OVERCOMING_LIMIT_SCORE_CONDITION:
                    def returnPosition = 2 * LIMIT_SCORE - nextPosition
                    playersMap[playerName] = returnPosition

                    moves playerName, actualPosition, LIMIT_SCORE
                    bouncedMessage playerName, returnPosition
                    break
                case WIN_CONDITION:
                    moves playerName, actualPosition, nextPosition
                    winnerMessage playerName
                    break
                case JUMP_CONDITION:
                    playersMap[playerName] = AFTER_JUMP_POSITION
                    warpMessage playerName, actualPosition, AFTER_JUMP_POSITION
                    break
                default:
                    playersMap[playerName] = nextPosition
                    moves playerName, actualPosition, nextPosition
            }


        }


    }

    def prankIt(messageBuilderDelegate, indexToPrank, actualPosition) {
        def userToPrank = playersMap.keySet()[indexToPrank]
        def positionToPrank = playersMap[userToPrank]

        messageBuilderDelegate.prank userToPrank, positionToPrank, actualPosition

    }

    def isOccupiedPrankIt(int nextPosition) {
        playersMap.findIndexOf { it.value == nextPosition }
    }

    private def checkIfPlayerOnGoose(messageBuilderDelegate, int nextPosition, int dice1, int dice2, String playerName) {

        if (!GOOSE_POSITION_LIST.contains(nextPosition)) {
            return messageBuilderDelegate.resultMessage
        } else {
            def nextNewPosition = nextPosition + dice1 + dice2
            messageBuilderDelegate.theGoseMove playerName, nextNewPosition
            playersMap[playerName] = nextNewPosition
            checkIfPlayerOnGoose(messageBuilderDelegate, nextNewPosition, dice1, dice2, playerName)
        }
    }

    private int getDiceValueFromUserOrForcedElseRandomly(int forceDice, ArrayList moveActionList, int diceNumber) {
        if (forceDice) {
            return forceDice
        } else if (moveActionList && moveActionList[diceNumber])
            return moveActionList[diceNumber] as Integer
        else {
            return Math.floor((6 - 0) * Math.random()) + 1
        }
    }


    def cheaterOnlyForTest(cheatAction) {
        def cheatActionList = cheatAction.findAll(/\w+/) as List<String>
        playersMap[cheatActionList[0]] = cheatActionList[1] as Integer

        return "Cheater! Current space of ${cheatActionList[0]} is ${playersMap[cheatActionList[0]].value}"

    }


}
