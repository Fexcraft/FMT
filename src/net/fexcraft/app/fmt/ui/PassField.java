package net.fexcraft.app.fmt.ui;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class PassField extends Field {

	private String value;

	public PassField(float width){
		super(FieldType.PASS, width);
	}

	private void fillpass(){
		int l = value.length();
		StringBuilder s = new StringBuilder();
		for(int i = 0; i < l; i++) s.append("*");
		super.text(s.toString());
	}

	@Override
	public void clear_text(){
		super.clear_text();
		value = "";
	}

	@Override
	public Element text(Object ntext){
		value = ntext == null ? "" : ntext.toString();
		fillpass();
		return this;
	}

	@Override
	public String get_pass(){
		return value;
	}

	@Override
	protected void backspace(String txt){
		value = value.substring(0, value.length() - 1);
		fillpass();
	}

	@Override
	public void onCharInput(int cha){
		value += new String(Character.toChars(cha));
		fillpass();
	}

}
