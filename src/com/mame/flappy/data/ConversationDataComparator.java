package com.mame.flappy.data;

import java.util.Comparator;

public class ConversationDataComparator implements Comparator<MessageItemData> {

	public ConversationDataComparator() {

	}

	@Override
	public int compare(MessageItemData arg0, MessageItemData arg1) {
		long first = arg0.getPostedDate();
		long next = arg1.getPostedDate();
		if (first == next) {
			return 0;
		} else if (first > next) {
			return 1;
		} else if (first < next) {
			return -1;
		} else {
			return 0;
		}
	}
}
