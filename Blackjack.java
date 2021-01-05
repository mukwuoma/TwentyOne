package blackjack;

import java.util.*;

public class Blackjack implements BlackjackEngine {

	private Random randomGenerator;
	private int numberOfDecks, gameStatus, playerAcc = 200, bet = 5;
	private ArrayList<Card> playerCards, dealerCards, deck;

	/**
	 * @param randomGenerator
	 * @param numberOfDecks
	 */
	public Blackjack(Random randomGenerator, int numberOfDecks) {

		this.randomGenerator = randomGenerator;
		this.numberOfDecks = numberOfDecks;

	}

	public int getNumberOfDecks() {
		return numberOfDecks;

	}

	public void createAndShuffleGameDeck() {

		deck();

		Collections.shuffle(deck, randomGenerator);

	}

	public Card[] getGameDeck() {

		return cards(deck);
	}

	public void deal() {

		createAndShuffleGameDeck();
		playerCards = new ArrayList<Card>();
		dealerCards = new ArrayList<Card>();

		int i = 0;

		while (i < 4) {

			if (i % 2 == 0) {
				playerCards.add(deck.get(0));
				deck.remove(0);
			} else {
				dealerCards.add(deck.get(0));
				deck.remove(0);
			}
			i++;
		}
		dealerCards.get(0).setFaceDown();

		playerAcc -= bet;
		gameStatus = GAME_IN_PROGRESS;
		setBetAmount(0);
		numberOfDecks--;
	}

	public Card[] getDealerCards() {

		return cards(dealerCards);
	}

	public int[] getDealerCardsTotal() {

		return total(dealerCards);

	}

	public int getDealerCardsEvaluation() {

		return evaluator(dealerCards);
	}

	public Card[] getPlayerCards() {

		return cards(playerCards);

	}

	public int[] getPlayerCardsTotal() {

		return total(playerCards);

	}

	public int getPlayerCardsEvaluation() {

		return evaluator(playerCards);

	}

	public void playerHit() {

		playerCards.add(deck.get(0));
		deck.remove(0);

		if (isBust(playerCards)) {
			dealerCards.get(0).setFaceUp();
			gameStatus = DEALER_WON;
		} else {
			gameStatus = GAME_IN_PROGRESS;
		}
	}

	public void playerStand() {
		int i = 0;
		dealerCards.get(0).setFaceUp();

		if (isBlackjack(dealerCards)) {
			gameStatus = DEALER_WON;
			playerAcc -= bet;
		} else if (isBlackjack(playerCards)) {
			gameStatus = PLAYER_WON;
			playerAcc += (bet * 2);
		} else if (isBlackjack(playerCards) && isBlackjack(dealerCards)) {
			gameStatus = DRAW;
			playerAcc += bet;
		}

		while (!isBust(dealerCards) && isLessThan(dealerCards, 16)) {
			dealerCards.get(i).setFaceUp();
			if (!isBlackjack(dealerCards)) {
				dealerCards.get(i).setFaceUp();
				dealerCards.add(deck.get(0));
				deck.remove(0);
				if (sum(dealerCards) >= 16 && sum(dealerCards) <= 21) {
					break;

				} else if (isBust(dealerCards)) {
					gameStatus = PLAYER_WON;
					playerAcc += (bet * 2);
					break;
				}
				i++;
			}
		}

		if (!isBust(dealerCards) && (!isBlackjack(playerCards) && !isBlackjack(dealerCards))) {

			if (sum(playerCards) > sum(dealerCards)) {

				gameStatus = PLAYER_WON;
				playerAcc += (bet * 2);

			} else if (sum(dealerCards) > sum(playerCards)) {
				gameStatus = DEALER_WON;
				playerAcc -= bet;

			} else if (sum(playerCards) == sum(dealerCards)) {
				gameStatus = DRAW;
				playerAcc += bet;
			}
		}

	}

	public int getGameStatus() {

		return gameStatus;
	}

	public void setBetAmount(int amount) {

		bet = amount;
	}

	public int getBetAmount() {

		return bet;
	}

	public void setAccountAmount(int amount) {

		playerAcc = amount;
	}

	public int getAccountAmount() {

		return playerAcc;
	}

	private void deck() {

		deck = new ArrayList<Card>();

		for (CardSuit suit : CardSuit.values()) {
			for (CardValue value : CardValue.values()) {
				deck.add(new Card(value, suit));
			}
		}
	}

	private boolean isBlackjack(ArrayList<Card> cards) {

		Iterator<Card> itr = cards.listIterator();

		boolean hasPicCard = false;

		int i = 0;
		if (cards.size() == 2) {

			while (i < 2) {

				int current = itr.next().getValue().getIntValue();

				if (current == 10) {
					hasPicCard = true;
				}
				i++;
			}
		}
		if (hasAce(cards) && hasPicCard) {
			return true;
		}
		return false;
	}

	private boolean isLessThan(ArrayList<Card> cards, int sum) {

		if (sum(cards) < sum) {
			return true;
		} else {
			return false;
		}

	}

	private boolean hasAce(ArrayList<Card> cards) {

		Iterator<Card> itr = cards.listIterator();

		boolean hasAce = false;

		while (itr.hasNext()) {
			int current = itr.next().getValue().getIntValue();
			if (current == 1) {
				hasAce = true;
			}

		}
		return hasAce;

	}

	private boolean isBust(ArrayList<Card> cards) {

		if (sum(cards) > 21) {
			return true;
		} else {
			return false;
		}

	}

	private boolean has21(ArrayList<Card> cards) {

		if (sum(cards) == 21) {
			return true;
		} else {
			return false;
		}
	}

	private int sum(ArrayList<Card> cards) {
		int sum = 0;
		int count = 0;
		Iterator<Card> itr = cards.listIterator();

		while (itr.hasNext()) {
			int current = itr.next().getValue().getIntValue();
			if (!hasAce(cards)) {
				sum += current;
			} else {
				if (current == 1) {

					if (count == 0 && sum + 11 <= 21) {
						sum += 11;
					} else if (count == 0 && (sum + 11 > 21)) {
						sum += 1;
					} else if (count > 0) {
						sum += 1;
					}
					count++;

				} else {
					sum += current;

				}
			}
		}
		return sum;
	}

	private int aceSum(ArrayList<Card> cards) {

		int sum = 0;

		Iterator<Card> itr = cards.listIterator();

		while (itr.hasNext()) {
			int current = itr.next().getValue().getIntValue();
			sum += current;
		}

		return sum;

	}

	private int evaluator(ArrayList<Card> cards) {

		if (isBlackjack(cards)) {
			return BLACKJACK;

		} else if (isLessThan(cards, 21)) {
			return LESS_THAN_21;

		} else if (isBust(cards)) {
			return BUST;

		} else if (has21(cards) && !isBlackjack(cards)) {
			return HAS_21;

		} else {
			return 0;
		}
	}

	private int[] total(ArrayList<Card> cards) {
		int[] finalTotal;

		List<Integer> total = new ArrayList<>();

		if (!hasAce(cards) && (sum(cards) < 22)) {

			total.add(sum(cards));
			finalTotal = new int[1];
			finalTotal[0] = total.get(0);
			return finalTotal;

		} else if (hasAce(cards) && (sum(cards) < 22)) {

			total.add(sum(cards));
			total.add(aceSum(cards));
			Collections.sort(total);
			finalTotal = new int[2];
			finalTotal[0] = total.get(0);
			finalTotal[1] = total.get(1);
			return finalTotal;

		} else if (sum(cards) > 21) {
			return null;
		} else if (isBlackjack(cards)) {

			finalTotal = new int[2];
			finalTotal[0] = 11;
			finalTotal[1] = 21;
			return finalTotal;
		} else if (aceSum(cards) == sum(cards)) {
			finalTotal = new int[1];
			finalTotal[0] = sum(cards);

		}
		return null;
	}

	private Card[] cards(ArrayList<Card> cards) {
		Card[] card = new Card[cards.size()];
		card = cards.toArray(card);
		return card;

	}
}