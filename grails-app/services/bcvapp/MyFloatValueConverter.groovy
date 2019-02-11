package bcvapp
//import org.grails.databinding.converters.web.LocaleAwareNumberConverter
import org.grails.databinding.converters.ValueConverter
import java.text.NumberFormat

// class MyFloatValueConverter extends LocaleAwareNumberConverter { 
class MyFloatValueConverter implements ValueConverter {
	
//		@Override
//		protected NumberFormat getNumberFormatter() {
//			NumberFormat.getInstance(Locale.GERMAN)
//		}
//		@Override
		boolean canConvert(Object value) {
			value instanceof String
		}
	
		@Override
		Object convert(Object value) {
				def numberFormatter = NumberFormat.getInstance(Locale.ENGLISH)
				numberFormatter.parse((String)value).asType(getTargetType())
		}
	
		@Override
		Class<?> getTargetType() {
			Float
		}
	}

