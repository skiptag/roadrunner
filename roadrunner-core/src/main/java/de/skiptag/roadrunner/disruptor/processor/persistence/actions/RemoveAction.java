package de.skiptag.roadrunner.disruptor.processor.persistence.actions;

import de.skiptag.roadrunner.disruptor.event.RoadrunnerEvent;
import de.skiptag.roadrunner.persistence.Path;
import de.skiptag.roadrunner.persistence.Persistence;

public class RemoveAction {

	private Persistence	persistence;

	public RemoveAction(Persistence persistence) {
		this.persistence = persistence;
	}

	public void handle(RoadrunnerEvent event) {
		Path path = event.extractNodePath();

		persistence.remove(event.getChangeLog(), event.getAuth(), path);

	}
}
