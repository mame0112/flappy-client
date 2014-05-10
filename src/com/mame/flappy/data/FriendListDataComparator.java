package com.mame.flappy.data;

import java.util.Comparator;

public class FriendListDataComparator implements
		Comparator<FriendListUpdateData> {

	public FriendListDataComparator() {

	}

	@Override
	public int compare(FriendListUpdateData arg0, FriendListUpdateData arg1) {

		String firstStr = arg0.getNewMessageDate();
		String nextStr = arg1.getNewMessageDate();

		if (firstStr != null && nextStr != null) {
			long first = Long.valueOf(firstStr);
			long next = Long.valueOf(nextStr);
			if (first == next) {
				return 0;
			} else if (first > next) {
				return 1;
			} else if (first < next) {
				return -1;
			} else {
				return 0;
			}
		} else {
			return 0;
		}
	}
}
