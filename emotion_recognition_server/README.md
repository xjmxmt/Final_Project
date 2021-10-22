# Affect Lab

This lab will teach you the implementation level basics about detecting catgorical emotions such as anger, happiness, fearand confusion among others. Along with categorical emotions you'll also be able to extract continuous affection information in the valence-arousal-dominance domain.

Furthermore, you are expected to apply your newfound knowledge to the CS4270 course project-you may reuse the code provided in this lab or develop your own. At the very least we expect your agent to possess affect/emotion detection capabilities.

# Dependencies

Make sure the following dependencies are installed. We recommened using a new conda environment.

- python=3.8
- pytorch <= 1.8.1
- scikit-learn
- numpy
- pandas
- matplotlib

## Links to EMOTIC dataset

This project uses the <a href="http://sunai.uoc.edu/emotic/download.html">EMOTIC dataset</a> and follows the methodology as introduced in the paper <a href="https://arxiv.org/pdf/2003.13401.pdf">'Context based emotion recognition using EMOTIC dataset'</a>.

To make it easier for you we have hosted the EMOTIC dataset as well, if you intend to make use of it.

**Link to data**: https://tud365.sharepoint.com/:f:/s/ConversationalAgents2021/EoSyoFarxCJFoZrfkgft3iQBnpRRPZNavJ6La5ZfRhrFXw?e=61LcKf

If you do not wish to train the model from scratch, section 12 will show you how you can use pretrained versions of the model along with a few examples