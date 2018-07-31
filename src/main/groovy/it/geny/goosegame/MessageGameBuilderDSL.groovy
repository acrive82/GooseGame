package it.geny.goosegame

class MessageGameBuilderDSL {

    def resultMessage = ""

    static def build(closureMessage) {
        def messageBuildeDSL = new MessageGameBuilderDSL()
        closureMessage.delegate = messageBuildeDSL
        closureMessage()
    }


    def listPlayers(Map playersMap) {
        resultMessage += "players: ${playersMap.keySet().join(", ")}"
    }

    def existingPlayer(playerName) {
        resultMessage += "${playerName}: already existing player"

    }

    def rolls(playerName, dice1, dice2) {
        resultMessage += "${playerName} rolls ${dice1}, ${dice2}."
    }

    def moves(playerName, from, to) {
        insertSpaceAfterDot()
        resultMessage += "${playerName} moves from ${!from ? "Start" : from} to ${to}"
    }

    def prank(String playerName, int occupiedPosition, int actualPosition) {
        insertSpaceAfterDot()
        resultMessage += "On ${occupiedPosition} there is ${playerName}, who returns to ${actualPosition}"
    }

    def warpMessage(playerName, from, toBridge) {
        insertSpaceAfterDot()
        resultMessage += "${playerName} moves from ${!from ? "Start" : from} to The Bridge. Pippo jumps to ${toBridge}"
    }

    def winnerMessage(playerName) {
        insertSpaceAfterDot()
        resultMessage += "${playerName} Wins!!"
    }

    def theGoseMove(playerName, nextPosition) {
        resultMessage += ", The Goose. ${playerName} moves again and goes to ${nextPosition}"
    }

    def bouncedMessage(playerName, returnPosition) {
        insertSpaceAfterDot()
        resultMessage += "${playerName} bounces! ${playerName} returns to ${returnPosition}"
    }


    private String insertSpaceAfterDot() {
        resultMessage += resultMessage[resultMessage.length() - 1] == "." ? " " : ". "
    }


}
