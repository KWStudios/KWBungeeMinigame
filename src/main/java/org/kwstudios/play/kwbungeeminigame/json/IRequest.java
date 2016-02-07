package org.kwstudios.play.kwbungeeminigame.json;

import org.kwstudios.play.kwbungeeminigame.enums.BungeeMessageAction;

public interface IRequest {

	public BungeeMessageAction getAction();

	public boolean isRequest();

}
