package name.njbartlett.osgi.vaadin.demo;

import java.util.Date;
import java.util.Enumeration;

import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogListener;
import org.osgi.service.log.LogReaderService;
import org.osgi.service.log.LogService;

import aQute.bnd.annotation.component.Reference;

import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Table;

@aQute.bnd.annotation.component.Component(factory = "com.vaadin.Component/contribution", properties = "name"
		+ "=Log")
public class LogTab extends CustomComponent {
	private static final long serialVersionUID = 1L;
	static String[] fields = { "Time", "Level", "Message", "Exception" };
	final IndexedContainer container = new IndexedContainer();
	{
		for (String p : fields) {
			container.addContainerProperty(p, String.class, "");
		}
	}

	final Table log = new Table();


	public LogTab() {
		setCaption("Logging");
		setCompositionRoot(log);
		log.setContainerDataSource(container);
		log.setVisibleColumns(fields);
	}

	@Reference
	protected void setLogReader(LogReaderService logreader) {
		for (Enumeration<?> e = logreader.getLog(); e.hasMoreElements();) {
			addEntry((LogEntry) e.nextElement());
		}
		logreader.addLogListener(new LogListener() {
			public void logged(LogEntry entry) {
				addEntry(entry);
			}
		});
	}

	private void addEntry(LogEntry entry) {
		try {
			Object id = container.addItem();
			Date time = new Date(entry.getTime());
			container.getContainerProperty(id, "Time")
					.setValue(time.toString());
			container.getContainerProperty(id, "Level").setValue(
					getLevel(entry.getLevel()));
			if (entry.getMessage() != null)
				container.getContainerProperty(id, "Message").setValue(
						entry.getMessage());
			if (entry.getException() != null)
				container.getContainerProperty(id, "Exception").setValue(
						entry.getException().getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Object getLevel(int level) {
		switch (level) {
		case LogService.LOG_DEBUG:
			return "debug";
		case LogService.LOG_ERROR:
			return "error";
		case LogService.LOG_INFO:
			return "info";
		case LogService.LOG_WARNING:
			return "warning";

		default:
			return "?" + level;
		}
	}

	protected void unsetLogReader(LogReaderService logreader) {
		// who cares?
	}

}
