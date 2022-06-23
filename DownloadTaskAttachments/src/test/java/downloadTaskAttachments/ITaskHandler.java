package downloadTaskAttachments;

public interface ITaskHandler<T> {
	void perform(T work) throws Exception;
}
