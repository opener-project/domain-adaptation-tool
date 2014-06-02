# This script parses AnCora dep corpus (http://clic.ub.edu/corpus/en)
# to required Conll format for the Domain Adaptation Tool
# Author: Andoni Azpeitia (aazpeitia@vicomtech.org) - Vicomtech-IK4 (http://www.vicomtech.es/)

######################################################################################
# $ ancora2conll.pl /home/user/ancora/es /home/user/ancora/out -ne 1
# prints CONLL file with only lemma and PoS column
######################################################################################
use strict;

use encoding 'UTF-8';

my $NE = "-ne";
my $TRAIN_SIZE = 1308;
my $TEST_SIZE  = 164;

my $BIO_BEGIN = "B-";
my $BIO_IN    = "I-";
my $BIO_OUT   = "O";

if (scalar(@ARGV) < 4) {
	&printHelp();
}

my $input_dir        = $ARGV[0];
my $output_dir       = $ARGV[1];
my $output_train_dir = $ARGV[1]."/train";
my $output_dev_dir   = $ARGV[1]."/dev";
my $output_test_dir  = $ARGV[1]."/test";

if ($ARGV[2] != $NE) {
	&printHelp();
}
my $ne = $ARGV[3];

# create dirs
unless(-e $output_dir or mkdir $output_dir) {
	die "Unable to create $output_dir";
}
unless(-e $output_train_dir or mkdir $output_train_dir) {
	die "Unable to create $output_train_dir";
}
unless(-e $output_dev_dir or mkdir $output_dev_dir) {
	die "Unable to create $output_dev_dir";
}
unless(-e $output_test_dir or mkdir $output_test_dir) {
	die "Unable to create $output_test_dir";
}

opendir(INPUT_DIR, $input_dir) or die $!;

my $cont = 0;
while (my $input_filename = readdir(INPUT_DIR)) {
	if ($input_filename !~ m/^.*~$/ && $input_filename !~ m/^\.+$/) {
		my $input_file = $input_dir."/".$input_filename;

		# open files
		open(INPUT_FILE, $input_file) or die $!;
		if ($cont < $TRAIN_SIZE) {
			open (OUTPUT_FILE, ">".$output_train_dir."/".$input_filename);
		}
		elsif ($cont < $TRAIN_SIZE+$TEST_SIZE) {
			open (OUTPUT_FILE, ">".$output_dev_dir."/".$input_filename);
		}
		else {
			open (OUTPUT_FILE, ">".$output_test_dir."/".$input_filename);
		}
		binmode(OUTPUT_FILE, ":utf8");

		my $lastIsEntity = 0;
		while (my $line = <INPUT_FILE>) {
			chomp($line);
			if ($line =~ m/^#/ || length($line) < 10) {
				print OUTPUT_FILE "$line\n";
				$lastIsEntity = 0;
			}
			else {
				my @columns = split("\t", $line);
				if (scalar(@columns) == 1) {
					@columns = split(" ", $line);
				}
				my $pos = &convert2Kaf($columns[3]);
				my $text = "";
				if ($ne eq "0") {
					#          index            word             lemma          pos          head
					$text .= $columns[0]."\t".$columns[1]."\t".$columns[2]."\t".$pos."\t".$columns[4];
				}
				else {
					my $entity = $columns[7];
					if ($entity =~ m/ne=(\w+)/) { # is an entity
						my $type = $1;
						if ($lastIsEntity == 0) { # put IN tag
							#          index            word             lemma          pos          head         entity
							$text .= $columns[0]."\t".$columns[1]."\t".$columns[2]."\t".$pos."\t".$columns[4]."\t".$BIO_IN.$type;
						}
						else { # put B tag
							#          index            word             lemma          pos          head         entity
							$text .= $columns[0]."\t".$columns[1]."\t".$columns[2]."\t".$pos."\t".$columns[4]."\t".$BIO_BEGIN.$type;
						}
						$lastIsEntity = 1;
					}
					else {  # is not an entity
						#          index            word             lemma          pos          head         entity
						$text .= $columns[0]."\t".$columns[1]."\t".$columns[2]."\t".$pos."\t".$columns[4]."\t".$BIO_OUT;
						$lastIsEntity = 0;
					}
				}
				print OUTPUT_FILE "$text\n";
			} # line printed
		} # file processed
		close(OUTPUT_FILE);
		$cont++;
	}
}

sub convert2Kaf {
	my $ancoraPos = shift(@_);
	my $kafPos = "O";
	if ($ancoraPos =~ m/^V/ || $ancoraPos =~ m/^v/) {
		$kafPos = "V";
	}
	elsif ($ancoraPos =~ m/^NC/ || $ancoraPos =~ m/^nc/) {
		$kafPos = "N";
	}
	elsif ($ancoraPos =~ m/^NP/ || $ancoraPos =~ m/^np/) {
		$kafPos = "R";
	}
	elsif ($ancoraPos =~ m/^A/ || $ancoraPos =~ m/^a/) {
		$kafPos = "G";
	}
	elsif ($ancoraPos =~ m/^R/ || $ancoraPos =~ m/^r/) {
		$kafPos = "A";
	}
	elsif ($ancoraPos =~ m/^D/ || $ancoraPos =~ m/^d/) {
		$kafPos = "D";
	}
	elsif ($ancoraPos =~ m/^S/ || $ancoraPos =~ m/^s/) {
		$kafPos = "P";
	}
	elsif ($ancoraPos =~ m/^P/ || $ancoraPos =~ m/^p/) {
		$kafPos = "Q";
	}
	elsif ($ancoraPos =~ m/^C/ || $ancoraPos =~ m/^c/) {
		$kafPos = "C";
	}
	return $kafPos;
}

sub printHelp {
	print "Usage: perl ancora2conll.pl corpus_input_dir corpus_output_dir -ne 0|1\n";
  	print "       -ne 0: does not parse named entites\n";
  	print "           1: parses named entites for test purposes\n";
  	exit;
}
